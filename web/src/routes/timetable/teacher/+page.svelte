<script>
  import * as Select from '$lib/elements/ui/select';
  import Lesson from '$lib/components/ui/lesson/lesson.svelte';
  import Timeslot from '$lib/elements/ui/timeslot/timeslot.svelte';
  import * as Tabs from '$lib/elements/ui/tabs/index';

  const classes = [
    { value: '5A', label: '5A' },
    { value: '5B', label: '5B' },
    { value: '5B', label: '5B' },
    { value: '5D', label: '5D' },
    { value: '6A', label: '6A' },
  ];
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

  <div class="inline-grid grid-flow-row gap-2 grid-cols-[max-content,repeat(5,1fr)] bg-white p-4 rounded-2xl w-full">
    <Timeslot />
    <Lesson color="orange" leftCorner="Tur" length={2} rightCorner="102" title="Deutsch" />
    <Lesson color="purple" leftCorner="Tur" rightCorner="102" title="Deutsch" />
    <Lesson color="red" leftCorner="Tur" rightCorner="102" title="Deutsch" />
    <Lesson color="green" leftCorner="Tur" rightCorner="102" title="Deutsch" />
    <Lesson color="aquamarine" leftCorner="Tur" rightCorner="102" title="Deutsch" />
    <Timeslot begin="8:35" end="9:20" index="2." />
    <Lesson color="aquamarine" leftCorner="Tur" rightCorner="102" title="Deutsch" />
    <Lesson color="aquamarine" leftCorner="Tur" length={2} rightCorner="102" title="Deutsch" />
    <Lesson color="aquamarine" leftCorner="Tur" rightCorner="102" title="Deutsch" />
    <Lesson color="aquamarine" leftCorner="Tur" rightCorner="102" title="Deutsch" />
    <Timeslot begin="9:25" end="10:10" index="3." />
    <Lesson color="blue" leftCorner="Tur" rightCorner="102" title="Deutsch" />
    <Lesson color="lightBlue" leftCorner="Tur" rightCorner="102" title="Deutsch" />
    <Lesson color="blue" leftCorner="Tur" rightCorner="102" title="Deutsch" />
    <Lesson color="brown" leftCorner="Tur" rightCorner="102" title="Deutsch" />
    <div class="col-span-full"></div>
    <Timeslot begin="10:30" end="11:15" index="4." />
    <Lesson color="lightBlue" leftCorner="Tur" rightCorner="102" title="Deutsch" />
    <Lesson color="brown" leftCorner="Tur" rightCorner="102" title="Deutsch" />
    <Lesson color="blue" leftCorner="Tur" rightCorner="102" title="Deutsch" />
    <Lesson color="yellow" leftCorner="Tur" rightCorner="102" title="Deutsch" />
    <Lesson color="blue" leftCorner="Tur" rightCorner="102" title="Deutsch" />
    <Timeslot begin="11:15" end="12:00" index="5." />
    <Lesson color="blue" leftCorner="Tur" rightCorner="102" title="Deutsch" />
    <Lesson color="red" leftCorner="Tur" rightCorner="102" title="Deutsch" />
    <Lesson color="lightBlue" leftCorner="Tur" rightCorner="102" title="Deutsch" />
    <Lesson color="brown" leftCorner="Tur" rightCorner="102" title="Deutsch" />
    <Lesson color="yellow" leftCorner="Tur" rightCorner="102" title="Deutsch" />
    <div class="col-span-full"></div>

    <Timeslot begin="12:20" end="13:05" index="6." />
    <Lesson color="aquamarine" leftCorner="Tur" rightCorner="102" title="Deutsch" />
    <Lesson color="orange" leftCorner="Tur" rightCorner="102" title="Deutsch" />
    <Lesson color="brown" leftCorner="Tur" rightCorner="102" title="Deutsch" />
    <Lesson color="lightBlue" leftCorner="Tur" rightCorner="102" title="Deutsch" />
    <Lesson color="blue" leftCorner="Tur" rightCorner="102" title="Deutsch" />
  </div>
</div>
