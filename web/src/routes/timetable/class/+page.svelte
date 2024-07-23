<script lang="ts">
  import { goto } from '$app/navigation';
  import type { Color } from '$lib/components/lesson/lesson';
  import { getTimetableMatrix, mergeLessons } from '$lib/components/timetable/timetable';
  import Timetable from '$lib/components/timetable/timetable.svelte';
  import { Day, getTeacherLessons, getTimeslots } from '$lib/sdk/fetch-client';
  import { Tabs, Select } from 'bits-ui';

  const classes = [
    { value: '5A', label: '5A' },
    { value: '5B', label: '5B' },
    { value: '5B', label: '5B' },
    { value: '5D', label: '5D' },
    { value: '6A', label: '6A' },
  ];

  const dayOrder = {
    [Day.Monday]: 0,
    [Day.Tuesday]: 1,
    [Day.Wednesday]: 2,
    [Day.Thursday]: 3,
    [Day.Friday]: 4,
    [Day.Saturday]: 5,
    [Day.Sunday]: 6,
  };

  const getLessons = async () => {
    const response = await getTeacherLessons('');
    const timeslots = await getTimeslots();
    const maxSlot = Math.max(...timeslots.map((timeslot) => timeslot.slot));

    const result = getTimetableMatrix(maxSlot, 5);

    for (const lesson of response.lessons) {
      const subject = response.subjects.find((subject) => subject.id === lesson.subjectId)!;
      const room = response.rooms.find((room) => room.id === lesson.roomId)!;
      const teacher = response.teachers.find((teacher) => teacher.id === lesson.teacherId)!;

      result[lesson.timeslot.slot][dayOrder[lesson.timeslot.day]] = {
        title: { text: subject.name, onClick: async () => goto(`/admin/subjects/${subject.id}`) },
        bottomLeft: { text: teacher.acronym, onClick: async () => goto(`/admin/teachers/${teacher.id}`) },
        bottomRight: {
          text: `${room.buildingName} — ${room.name}`,
          onClick: async () => goto(`/admin/rooms/${room.id}`),
        },
        color: subject.color as Color | undefined,
        lessonId: lesson.id,
        length: 1,
      };
    }

    return mergeLessons(result);
  };
</script>

<div class="flex flex-col gap-2">
  <div class="flex flex-row w-full justify-between">
    <Tabs.Root class="w-[20%] min-w-[200px]" value="class">
      <Tabs.List class="grid w-full grid-cols-3">
        <Tabs.Trigger value="class">Klasse</Tabs.Trigger>
        <Tabs.Trigger value="teacher">Lehrer</Tabs.Trigger>
        <Tabs.Trigger value="room">Raum</Tabs.Trigger>
      </Tabs.List>
    </Tabs.Root>

    <div class="flex flex-row items-baseline gap-2">
      <label class="font-medium" for="chosenResource">Schüler</label>
      <Select.Root portal={null}>
        <Select.Trigger class="w-[10%] min-w-[200px]">
          <Select.Value placeholder="Wähle eine Klasse aus" />
        </Select.Trigger>
        <Select.Content>
          <Select.Group>
            {#each classes as classItem}
              <Select.Item value={classItem.value} label={classItem.label}>
                {classItem.label}
              </Select.Item>
            {/each}
          </Select.Group>
        </Select.Content>
        <Select.Input name="chosenResource" />
      </Select.Root>
    </div>
  </div>

  {#await getLessons() then items}
    <Timetable {items} />
  {/await}
</div>
