<script lang="ts">
  import {
    type ConstraintArgumentRequestDto,
    type ConstraintInstanceRequestDto,
    type ConstraintSignature,
    createConstraintInstance,
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
    data: Record<string, ComboBoxItem[]>,
  ) => Promise<void>;

  let data: Record<string, ComboBoxItem[]> = {};

  async function updateItems(value: string, name?: string, parameterType?: ParameterType) {
    const page: Pageable = { page: 0, size: 2 };
    const sort: Sort = {};
    if (name === undefined) {
      name = constraintSignature.parameters.find(
        (parameter) => parameter.parameterType === parameterType!,
      )!.parameterName!;
    }
    try {
      switch (parameterType) {
        case ParameterType.Grade: {
          const grades = await getGrades(page, { name: value });
          data[name] = grades.map((grade) => ({ value: grade.id, label: grade.name }));
          break;
        }
        case ParameterType.Subject: {
          const subjects = await getSubjects(page, { name: value });
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
          const tags = await getTags(page, { name: value });
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

<div class="flex flex-col gap-8 bg-primary w-fit p-6 rounded-md text-white">
  <p class="font-bold text-md">{constraintSignature.description}</p>

  {#await initData() then}
    {#each constraintSignature.parameters as parameter (parameter.id)}
      <div class="flex flex-row items-center justify-between w-full gap-8">
        <p>{parameter.parameterName}</p>

        <div class="text-primary">
          {#if parameter.parameterType === ParameterType.Teacher}
            <ComboBox
              onSearch={(value) => updateItems(value, parameter.parameterName, parameter.parameterType)}
              data={data[parameter.parameterName || '']}
            />
          {:else if parameter.parameterType === ParameterType.Room}
            <ComboBox
              onSearch={(value) => updateItems(value, parameter.parameterName, parameter.parameterType)}
              data={data[parameter.parameterName || '']}
            />
          {:else if parameter.parameterType === ParameterType.Grade}
            <ComboBox
              onSearch={(value) => updateItems(value, parameter.parameterName, parameter.parameterType)}
              data={data[parameter.parameterName || '']}
            />
          {:else if parameter.parameterType === ParameterType.Tag}
            <ComboBox
              onSearch={(value) => updateItems(value, parameter.parameterName, parameter.parameterType)}
              data={data[parameter.parameterName || '']}
            />
          {:else if parameter.parameterType === ParameterType.Timeslot}
            <ComboBox
              onSearch={(value) => updateItems(value, parameter.parameterName, parameter.parameterType)}
              data={data[parameter.parameterName || '']}
            />
          {:else if parameter.parameterType === ParameterType.StudentGroup}
            <ComboBox
              onSearch={(value) => updateItems(value, parameter.parameterName, parameter.parameterType)}
              data={data[parameter.parameterName || '']}
            />
          {:else if parameter.parameterType === ParameterType.Subject}
            <ComboBox
              onSearch={(value) => updateItems(value, parameter.parameterName, parameter.parameterType)}
              data={data[parameter.parameterName || '']}
            />
          {/if}
        </div>
      </div>
    {/each}
  {/await}

  <Button
    variant="outline"
    class="bg-accent border-0 text-md text-white py-6"
    on:click={async () => {
      addInstance(constraintSignature, data);
    }}>Hinzufügen</Button
  >
</div>
