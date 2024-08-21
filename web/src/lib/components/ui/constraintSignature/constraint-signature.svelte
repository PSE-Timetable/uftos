<script lang="ts">
  import {
    type ConstraintSignature,
    getGrades,
    getRooms,
    getStudentGroups,
    getStudents,
    getSubjects,
    getTags,
    getTeachers,
    getTimeslots,
    type Pageable,
    ParameterType,
    type Sort,
  } from '$lib/sdk/fetch-client';
  import { Button } from '$lib/elements/ui/button';
  import ComboBox, { type ComboBoxItem } from '$lib/elements/ui/combo-box/combo-box.svelte';

  export let constraintSignature: ConstraintSignature;
  export let addInstance: (
    constraintSignature: ConstraintSignature,
    selectedIds: Record<string, string>,
  ) => Promise<void>;

  let data: Record<string, ComboBoxItem[]> = {};
  let selectedIds: Record<string, string> = {};
  let selectedLabels: Record<string, string> = {};

  async function updateItems(value: string, name: string, parameterType: ParameterType) {
    const page: Pageable = { page: 0, size: 40 };
    const sort: Sort = { sort: ['name,asc'] };
    try {
      switch (parameterType) {
        case ParameterType.Grade: {
          const grades = await getGrades(sort, { name: value });
          data[name] = grades.map((grade) => ({ value: grade.id, label: grade.name }));
          break;
        }
        case ParameterType.Subject: {
          const subjects = await getSubjects(sort, { name: value });
          data[name] = subjects.map((subject) => ({ value: subject.id, label: subject.name }));
          break;
        }
        case ParameterType.Room: {
          const { content } = await getRooms(page, { name: value });
          data[name] =
            content?.map((room) => ({
              value: room.id,
              label: `${room.buildingName}: ${room.name}`,
            })) || [];
          break;
        }
        case ParameterType.StudentGroup: {
          const { content } = await getStudentGroups(page, { name: value });
          data[name] = content?.map((studentGroup) => ({ value: studentGroup.id, label: studentGroup.name })) || [];
          break;
        }
        case ParameterType.Student: {
          const { content } = await getStudents(page, { firstName: value });
          data[name] =
            content?.map((student) => ({
              value: student.id,
              label: `${student.firstName} ${student.lastName}`,
            })) || [];
          break;
        }
        case ParameterType.Tag: {
          const tags = await getTags(sort, { name: value });
          data[name] = tags.map((tag) => ({ value: tag.id, label: tag.name }));
          break;
        }
        case ParameterType.Teacher: {
          const { content } = await getTeachers(page, { firstName: value });
          data[name] =
            content?.map((teacher) => ({
              value: teacher.id,
              label: `${teacher.firstName} ${teacher.lastName}`,
            })) || [];
          break;
        }
        case ParameterType.Timeslot: {
          const timeslots = await getTimeslots();
          data[name] = timeslots.map((timeslot) => ({
            value: timeslot.id,
            label: `${timeslot.day}: ${timeslot.slot}`,
          }));
          break;
        }
      }
    } catch (error) {
      console.error('Error fetching data:', error);
    }
  }
  const initData = async () => {
    await Promise.all(
      constraintSignature.parameters.map(({ parameterName, parameterType }) =>
        updateItems('', parameterName, parameterType),
      ),
    );
  };
</script>

<div class="flex flex-col gap-8 bg-primary w-fit h-fit p-6 rounded-md text-white">
  <p class="font-bold text-md">
    {constraintSignature.description.replaceAll(
      /{(?<paramName>.*?)}/gm,
      (match, paramName) => selectedLabels[paramName] ?? match,
    )}
  </p>

  {#await initData() then}
    {#each constraintSignature.parameters as parameter (parameter.id)}
      <div class="flex flex-row items-center justify-between w-full gap-8">
        <p>{parameter.parameterName}</p>

        <div class="text-primary">
          <ComboBox
            onSearch={(value) => updateItems(value, parameter.parameterName, parameter.parameterType)}
            data={data[parameter.parameterName]}
            selectedId={selectedIds[parameter.parameterName]}
            onSelectChange={(value, label) => {
              selectedIds[parameter.parameterName] = value;
              selectedLabels[parameter.parameterName] = label;
            }}
          />
        </div>
      </div>
    {/each}
  {/await}

  <Button
    variant="outline"
    class="bg-accent border-0 text-md text-white py-6"
    on:click={() => addInstance(constraintSignature, selectedIds)}>Hinzufügen</Button
  >
</div>
