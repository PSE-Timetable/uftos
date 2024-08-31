<script lang="ts">
  import { goto } from '$app/navigation';
  import * as Tabs from '$lib/elements/ui/tabs/index';
  import ComboBox from '$lib/elements/ui/combo-box/combo-box.svelte';
  import {
    getClassTimetableLessons,
    getRoomTimetableLessons,
    getStudentTimetableLessons,
    getTeacherTimetableLessons,
    resourceDisplayName,
  } from '$lib/components/timetable/timetable.js';
  import {
    getRoomsItems,
    getStudentGroupsItems,
    getStudentsItems,
    getTeachersItems,
  } from '$lib/utils/combobox-items.js';
  import type { ComboBoxItem } from '$lib/elements/ui/combo-box/combo-box.js';
  import Timetable from '$lib/components/timetable/timetable.svelte';
  import {
    getRoomLessons,
    getStudentGroupLessons,
    getStudentLessons,
    getTeacherLessons,
  } from '$lib/sdk/fetch-client.js';

  export let data;
  let items: ComboBoxItem[] = [];
  let selectedId: string = '';

  $: id = data.id;
  $: filteredItems = items;
  $: selectedId = items.map(({ value }) => value).includes(selectedId) ? selectedId : items.at(0)?.value || '';

  const onTimetableSearch = (value: string) => {
    filteredItems = value ? items.filter((item) => item.label.toLowerCase().includes(value.toLowerCase())) : items;
  };

  const onSearch = async (search: string) => {
    switch (data.type) {
      case 'class': {
        data.resources.class = await getStudentGroupsItems({}, { search });
        break;
      }
      case 'teacher': {
        data.resources.teacher = await getTeachersItems({}, { search });
        break;
      }
      case 'room': {
        data.resources.room = await getRoomsItems({}, { search });
        break;
      }
      case 'student': {
        data.resources.student = await getStudentsItems({}, { search });
        break;
      }
    }
  };
</script>

<Tabs.Root
  value={data.type}
  onValueChange={(value) => goto(`/timetable/${value}`)}
  class="flex flex-col w-full justify-between p-4 gap-6"
>
  <div class="flex flex-row w-full justify-between items-start">
    <div class="flex w-full items-center justify-between gap-4">
      <Tabs.List class="grid grid-flow-col auto-cols-auto">
        <Tabs.Trigger value="class">{resourceDisplayName.class}</Tabs.Trigger>
        <Tabs.Trigger value="teacher">{resourceDisplayName.teacher}</Tabs.Trigger>
        <Tabs.Trigger value="room">{resourceDisplayName.room}</Tabs.Trigger>
        <Tabs.Trigger value="student">{resourceDisplayName.student}</Tabs.Trigger>
      </Tabs.List>
      <div class="flex items-center gap-4">
        <label class="font-medium" for="chosenResource">{resourceDisplayName[data.type]}</label>
        {#key data.type}
          <ComboBox
            {onSearch}
            data={data.resources[data.type]}
            selectedId={data.id ?? ''}
            onSelectChange={(value) => goto(`/timetable/${data.type}/${value}`)}
            placeholder={`${resourceDisplayName[data.type]} auswählen`}
            searchPlaceholder={`Suche ${resourceDisplayName[data.type]}`}
            notFoundPlaceholder={`Kein ${resourceDisplayName[data.type]} gefunden`}
          />
        {/key}
      </div>
    </div>
  </div>

  {#if !data.id}
    <div class="flex justify-center bg-white rounded-2xl p-4">Wähle einen Stundenplan aus!</div>
  {:else}
    <div class="flex flex-col gap-2">
      <ComboBox
        onSearch={onTimetableSearch}
        data={filteredItems}
        {selectedId}
        onSelectChange={(id) => (selectedId = id)}
        placeholder="Stundenplan auswählen"
        searchPlaceholder="Suche Stundenplan"
        notFoundPlaceholder="Kein Studenplan gefunden"
      />
      <Tabs.Content value="class">
        {#if data.type === 'class' && id}
          {#key id}
            <Timetable
              getLessons={async () => {
                const response = await getStudentGroupLessons(id);
                items = response.timetables.map(({ id, name }) => ({ value: id, label: name }));
                return await getClassTimetableLessons(response, selectedId);
              }}
            />
          {/key}
        {/if}
      </Tabs.Content>
      <Tabs.Content value="teacher">
        {#if data.type === 'teacher' && id}
          {#key id}
            <Timetable
              getLessons={async () => {
                const response = await getTeacherLessons(id);
                items = response.timetables.map(({ id, name }) => ({ value: id, label: name }));
                return await getTeacherTimetableLessons(response, selectedId);
              }}
            />
          {/key}
        {/if}
      </Tabs.Content>
      <Tabs.Content value="room">
        {#if data.type === 'room' && id}
          {#key id}
            <Timetable
              getLessons={async () => {
                const response = await getRoomLessons(id);
                items = response.timetables.map(({ id, name }) => ({ value: id, label: name }));
                return await getRoomTimetableLessons(response, selectedId);
              }}
            />
          {/key}
        {/if}
      </Tabs.Content>
      <Tabs.Content value="student">
        {#if data.type === 'student' && id}
          {#key id}
            <Timetable
              getLessons={async () => {
                const response = await getStudentLessons(id);
                items = response.timetables.map(({ id, name }) => ({ value: id, label: name }));
                return await getStudentTimetableLessons(response, selectedId);
              }}
            />
          {/key}
        {/if}
      </Tabs.Content>
    </div>
  {/if}
</Tabs.Root>
