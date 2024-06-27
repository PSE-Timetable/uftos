<script>
  import Checkbox from '$lib/elements/ui/checkbox/checkbox.svelte';
  import * as Select from '$lib/elements/ui/select';
  import * as Tabs from '$lib/elements/ui/tabs';
  import Lesson from '$lib/elements/ui/lesson/lesson.svelte';
  import Timeslot from '$lib/elements/ui/timeslot/timeslot.svelte';

  const classes = [
    { value: '5A', label: '5A' },
    { value: '5B', label: '5B' },
    { value: '5B', label: '5B' },
    { value: '5D', label: '5D' },
    { value: '6A', label: '6A' },
  ];
</script>

<div class="flex flex-row w-full justify-between">
  <Tabs.Root class="w-[400px]" value="account">
    <Tabs.List class="grid w-full grid-cols-2">
      <Tabs.Trigger value="account">Account</Tabs.Trigger>
      <Tabs.Trigger value="password">Password</Tabs.Trigger>
    </Tabs.List>
    <Tabs.Content value="Schüler">
      der
    </Tabs.Content>
    <Tabs.Content value="password">

    </Tabs.Content>
  </Tabs.Root>

  <Lesson color="orange" leftCorner="Tur" rightCorner="102" title="Deutsch" />

  <Select.Root portal={null}>
    <Select.Trigger class="w-[200px]">
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
    <Select.Input name="favoriteFruit" />
  </Select.Root>
</div>
<Timeslot />

<div class="h-[100px]"></div>
<!-- These empty divs are needed so that VITE doesn't let these bad apples fall.
     There has to be a better fix!
-->
<div class="row-span-3"></div>
<div class="row-span-4"></div>

<div
  class="inline-grid grid-flow-row gap-2 grid-cols-[max-content,repeat(5,1fr)] bg-white p-8 rounded-3xl w-full">
  <Timeslot />
  <Lesson color="orange" leftCorner="Tur" length={3} rightCorner="102" title="Deutsch" />
  <Lesson color="purple" leftCorner="Tur" rightCorner="102" title="Deutsch" />
  <Lesson color="red" leftCorner="Tur" rightCorner="102" title="Deutsch" />
  <Lesson color="green" leftCorner="Tur" rightCorner="102" title="Deutsch" />
  <Lesson color="aquamarine" leftCorner="Tur" rightCorner="102" title="Deutsch" />
  <Timeslot begin="8:35" end="9:20" index="2." />
  <Lesson color="aquamarine" leftCorner="Tur" rightCorner="102" title="Deutsch" />
  <Lesson color="aquamarine" leftCorner="Tur" rightCorner="102" title="Deutsch" />
  <Lesson color="aquamarine" leftCorner="Tur" rightCorner="102" title="Deutsch" />
  <Lesson color="aquamarine" leftCorner="Tur" rightCorner="102" title="Deutsch" />
  <Timeslot begin="9:25" end="10:10" index="3." />
  <Lesson color="blue" leftCorner="Tur" rightCorner="102" title="Deutsch" />
  <Lesson color="lightBlue" leftCorner="Tur" rightCorner="102" title="Deutsch" />
  <Lesson color="lightBlue" leftCorner="Tur" rightCorner="102" title="Deutsch" />
  <Lesson color="brown" leftCorner="Tur" rightCorner="102" title="Deutsch" />
  <div class="col-span-full"></div>
  <Timeslot begin="10:30" end="11:15" index="4." />
  <Lesson color="blue" leftCorner="Tur" rightCorner="102" title="Deutsch" />
  <Lesson color="lightBlue" leftCorner="Tur" rightCorner="102" title="Deutsch" />
  <Lesson color="brown" leftCorner="Tur" rightCorner="102" title="Deutsch" />
  <Lesson color="yellow" leftCorner="Tur" rightCorner="102" title="Deutsch" />
  <Lesson color="blue" leftCorner="Tur" rightCorner="102" title="Deutsch" />
</div>