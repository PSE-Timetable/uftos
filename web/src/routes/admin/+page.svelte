<script lang="ts">
  import Card from '$lib/components/ui/card/card.svelte';
  import { Icons } from '$lib/utils';
  import LinkBar from '$lib/components/ui/link-bar/link-bar.svelte';
  import DataTable from '$lib/elements/ui/dataTable/data-table.svelte';
  import { getServerStats } from '$lib/sdk/fetch-client';
  import { onMount } from 'svelte';
  import * as Select from '$lib/elements/ui/select';
  import {
    deleteGradeEntry,
    deleteRoomEntry,
    deleteStudentEntry,
    deleteTeacherEntry,
    loadGrades,
    loadRoomPage,
    loadStudentPage,
    loadTeacherPage,
  } from '$lib/utils/resources';

  let teacherColumnNames = ['Vorname', 'Nachname', 'Akronym', 'Fächer', 'Tags'];
  let teacherKeys = ['id', 'firstName', 'lastName', 'acronym', 'subjects', 'tags'];
  let studentColumnNames = ['Vorname', 'Nachname', 'Tags'];
  let studentKeys = ['id', 'firstName', 'lastName', 'tags'];
  let gradeColumnNames = ['Name', 'Tags'];
  let gradeKeys = ['id', 'name', 'tags'];
  let roomColumnNames = ['Name', 'Gebäude', 'Kapazität', 'Tags'];
  let roomKeys = ['id', 'name', 'buildingName', 'capacity', 'tags'];
  let pageLoaded = false;

  const settings = [
    { value: 'edit_constraints', label: 'Constraints ändern', url: '/admin/constraints' },
    { value: 'generate_timetable', label: 'Stundenplan generieren', url: '/admin/timetable' },
    { value: 'edit_curriculum', label: 'Curriculum anpassen', url: '/admin/curriculums' },
    { value: 'edit_dsl', label: 'DSL Editor', url: '/admin/editor' },
    { value: 'general_settings', label: 'Allgemeine Einstell.', url: '/admin/settings' },
  ];

  onMount(() => {
    pageLoaded = true;
  });
</script>

<div class="h-[170px] w-full bg-primary p-4 items-center absolute">
  <div class="mb-8 flex flex-row justify-between text-white font-medium text-2xl">
    <div>
      <LinkBar />
    </div>

    <Select.Root portal={null}>
      <Select.Trigger class="w-fit flex flex-row gap-2 justify-between">
        <Select.Value class="text-primary text-lg" placeholder="Einstellungen" />
      </Select.Trigger>
      <Select.Content>
        <Select.Group>
          {#each settings as setting}
            <Select.Item
              value={setting.value}
              label={setting.label}
              on:click={() => {
                window.location.href = setting.url;
              }}>{setting.label}</Select.Item
            >
          {/each}
        </Select.Group>
      </Select.Content>
      <Select.Input name="settings" />
    </Select.Root>
  </div>

  <div class="w-full mb-12 flex flex-row gap-14 justify-between text-white font-medium text-2xl">
    {#await getServerStats() then stats}
      <Card text="Schüler" icon={Icons.STUDENT} number={stats.classCount} url="/admin/students" />
      <Card text="Lehrer" icon={Icons.TEACHER} number={stats.teacherCount} url="/admin/teachers" />
      <Card text="Stufen" icon={Icons.GRADE} number={stats.teacherCount} url="/admin/grades" />
      <Card text="Räume" icon={Icons.ROOM} number={stats.roomCount} url="/admin/rooms" />
      <Card text="Constraints" icon={Icons.CONSTRAINT} number={stats.classCount} url="/admin/constraints" />
    {/await}
  </div>
  {#if pageLoaded}
    <div class="flex flex-col gap-16 text-foreground">
      <div class="flex flex-col gap-4">
        <p class="font-bold text-2xl">Schüler</p>
        <DataTable
          columnNames={studentColumnNames}
          keys={studentKeys}
          loadPage={loadStudentPage}
          deleteEntry={deleteStudentEntry}
          pageSize={5}
          addButton={false}
          editAvailable={false}
        />
      </div>
      <div class="flex flex-col gap-4">
        <p class="font-bold text-2xl">Lehrer</p>
        <DataTable
          columnNames={teacherColumnNames}
          keys={teacherKeys}
          loadPage={loadTeacherPage}
          deleteEntry={deleteTeacherEntry}
          pageSize={5}
          addButton={false}
          editAvailable={false}
        />
      </div>
      <div class="flex flex-col gap-4">
        <p class="font-bold text-2xl">Stufen</p>
        <DataTable
          columnNames={gradeColumnNames}
          keys={gradeKeys}
          loadPage={loadGrades}
          deleteEntry={deleteGradeEntry}
          pageSize={5}
          addButton={false}
          editAvailable={false}
        />
      </div>
      <div class="flex flex-col gap-4">
        <p class="font-bold text-2xl">Räume</p>
        <DataTable
          columnNames={roomColumnNames}
          keys={roomKeys}
          loadPage={loadRoomPage}
          deleteEntry={deleteRoomEntry}
          pageSize={5}
          addButton={false}
          editAvailable={false}
        />
      </div>
    </div>
  {/if}
</div>
