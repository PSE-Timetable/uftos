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
    type GradeResponseDto,
    type Pageable,
    ParameterType,
    type Subject,
    type Teacher,
  } from '$lib/sdk/fetch-client';
  import { Button } from '$lib/elements/ui/button';
  import ComboBox, { type ComboBoxItem } from '$lib/elements/ui/combo-box/combo-box.svelte';

  export let constraintSignature: ConstraintSignature;
  export let teachers: Teacher[] | undefined;
  export let grades: GradeResponseDto[] | undefined;
  export let subjects: Subject[] | undefined;

  let data: Record<string, ComboBoxItem[]> = {};

  async function updateItems(value: string, name?: string, parameterType?: ParameterType) {
    const page: Pageable = { page: 0, size: 2 };
    if (name === undefined) {
      name = constraintSignature.parameters.find(
        (parameter) => parameter.parameterType === parameterType!,
      )!.parameterName!;
    }
    try {
      switch (parameterType) {
        case ParameterType.Grade: {
          const { content } = value ? await getGrades(page, { name: value }) : { content: grades };
          data[name] = content?.map((grade) => ({ value: grade.id, label: grade.name ?? '' })) || [];
          break;
        }
        case ParameterType.Subject: {
          const { content } = value ? await getSubjects(page, { name: value }) : { content: subjects };
          data[name] = content?.map((subject) => ({ value: subject.id, label: subject.name })) || [];
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
          const { content } = await getTags(page, { name: value });
          data[name] = content?.map((tag) => ({ value: tag.id, label: tag.name })) || [];
          break;
        }
        case ParameterType.Teacher: {
          const { content } = value ? await getTeachers(page, { firstName: value }) : { content: teachers };
          data[name] =
            content?.map((teacher) => ({
              value: teacher.id,
              label: `${teacher.firstName} ${teacher.lastName}`,
            })) || [];
          break;
        }
        case ParameterType.Timeslot: {
          const { content } = await getTimeslots(page);
          data[name] =
            content?.map((timeslot) => ({
              value: timeslot.id,
              label: `${timeslot.day}: ${timeslot.slot}`,
            })) || [];
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

  <Button variant="outline" class="bg-accent border-0 text-md text-white py-6">Hinzufügen</Button>
</div>
