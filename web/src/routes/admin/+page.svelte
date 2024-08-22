<script lang="ts">
  import { toast } from 'svelte-sonner';
  import Button from './../../lib/elements/ui/button/button.svelte';
  import { goto } from '$app/navigation';
  import { ChevronLeft } from 'lucide-svelte/icons';
  import Card from '$lib/components/ui/card/card.svelte';
  import { Icons } from '$lib/utils';
  import LinkBar from '$lib/components/ui/link-bar/link-bar.svelte';
  import DataTable from '$lib/elements/ui/dataTable/data-table.svelte';
  import * as DropdownMenu from '$lib/elements/ui/dropdown-menu/index.js';
  import { ChevronDown } from 'lucide-svelte';
  import { createTimetable } from '$lib/sdk/fetch-client';
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

  export let data;

  let teacherColumnNames = ['Vorname', 'Nachname', 'Akronym', 'Fächer', 'Tags'];
  let teacherKeys = ['id', 'firstName', 'lastName', 'acronym', 'subjects', 'tags'];
  let studentColumnNames = ['Vorname', 'Nachname', 'Tags'];
  let studentKeys = ['id', 'firstName', 'lastName', 'tags'];
  let gradeColumnNames = ['Name', 'Tags'];
  let gradeKeys = ['id', 'name', 'tags'];
  let roomColumnNames = ['Name', 'Gebäude', 'Kapazität', 'Tags'];
  let roomKeys = ['id', 'name', 'buildingName', 'capacity', 'tags'];

  const settings = [
    { value: 'edit_constraints', label: 'Constraints ändern', url: '/admin/constraints' },
    { value: 'edit_curriculum', label: 'Curriculum anpassen', url: '/admin/curriculums' },
    { value: 'edit_dsl', label: 'DSL Editor', url: '/admin/editor' },
    { value: 'general_settings', label: 'Allgemeine Einstell.', url: '/admin/settings' },
  ];
</script>

<div class="h-[170px] w-[100%] min-w-[fit-content] bg-primary p-4 items-center absolute">
  <div class="mb-8 flex flex-row gap ì-2 justify-between text-white font-medium text-2xl items-center">
    <div class="flex flex-row">
      <Button on:click={() => goto('./')} variant="secondary" size="icon" class="rounded-full bg-accent mr-6">
        <ChevronLeft class="h-5 w-5 text-white" />
      </Button>

      <LinkBar />
    </div>

    <DropdownMenu.Root>
      <DropdownMenu.Trigger asChild let:builder>
        <Button class="bg-white text-primary text-lg hover:bg-accent hover:text-white" builders={[builder]}>
          <div class="flex flex-row gap-2 items-center">
            Einstellungen
            <ChevronDown />
          </div>
        </Button>
      </DropdownMenu.Trigger>
      <DropdownMenu.Content class="w-56">
        <DropdownMenu.Group>
          {#each settings as setting}
            <DropdownMenu.Item
              class="text-lg"
              on:click={() => {
                window.location.href = setting.url;
              }}>{setting.label}</DropdownMenu.Item
            >
          {/each}
          <DropdownMenu.Item
            class="text-lg"
            on:click={async () => {
              const name = new Date().getFullYear() + ':' + Date.now();
              const response = await createTimetable({ name });
              if (response.success) {
                toast.success('Erfolgreich', {
                  description: response.message,
                });
              } else {
                toast.error('Fehler', {
                  description: response.message,
                });
              }
            }}>Stundenplan generieren</DropdownMenu.Item
          >
        </DropdownMenu.Group>
      </DropdownMenu.Content>
    </DropdownMenu.Root>
  </div>

  <div class="w-full mb-12 flex flex-row gap-14 justify-between text-white font-medium text-2xl">
    <Card text="Schüler" icon={Icons.STUDENT} number={data.stats.studentCount} url="/admin/students" />
    <Card text="Lehrer" icon={Icons.TEACHER} number={data.stats.teacherCount} url="/admin/teachers" />
    <Card text="Stufen" icon={Icons.GRADE} number={data.stats.gradeCount} url="/admin/grades" />
    <Card text="Räume" icon={Icons.ROOM} number={data.stats.roomCount} url="/admin/rooms" />
    <Card text="Constraints" icon={Icons.CONSTRAINT} number={data.stats.constraintCount} url="/admin/constraints" />
  </div>
  <div class="flex flex-col gap-16 pb-4">
    <div class="flex flex-col gap-4">
      <p class="font-bold text-2xl">Schüler</p>
      <DataTable
        initialData={data.initialStudents}
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
        initialData={data.initialTeachers}
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
        initialData={data.initialGrades}
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
        initialData={data.initialRooms}
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
</div>
