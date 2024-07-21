<script lang="ts">
  import Card from '$lib/components/ui/card/card.svelte';
  import { Icons } from '$lib/utils';
  import LinkBar from '$lib/components/ui/link-bar/link-bar.svelte';
  import DataTable, { type DataItem } from '$lib/elements/ui/dataTable/data-table.svelte';
  import {
    deleteTeacher,
    getServerStats,
    getTeachers,
    type Pageable,
    type PageTeacher,
    type ServerStatisticsResponseDto,
  } from '$lib/sdk/fetch-client';
  import { error } from '@sveltejs/kit';
  import { onMount } from 'svelte';
  import * as Select from '$lib/elements/ui/select';

  let columnNames = ['Vorname', 'Nachname', 'Akronym', 'Fächer', 'Tags'];
  let keys = ['id', 'firstName', 'lastName', 'acronym', 'subjects', 'tags'];
  let pageLoaded = false;

  const settings = [
    { value: 'edit_constraints', label: 'Constraints ändern', url: '/admin/constraints' },
    { value: 'generate_timetable', label: 'Stundenplan generieren', url: '/admin/timetable' },
    { value: 'edit_curriculum', label: 'Curriculum anpassen', url: '/admin/curriculums' },
    { value: 'edit_dsl', label: 'DSL Editor', url: '/admin/editor' },
    { value: 'general_settings', label: 'Allgemeine Einstell.', url: '/admin/settings' },
  ];

  let stats: ServerStatisticsResponseDto;

  onMount(async () => {
    pageLoaded = true;
    stats = await loadStats();
  });

  async function loadStats() {
    const response = await getServerStats();
    return response;
  }

  async function loadPage(index: number, sortString: string, filter: string) {
    let pageable: Pageable = { page: index, size: 10, sort: [sortString] };
    try {
      const result: PageTeacher = await getTeachers(pageable, {
        firstName: filter,
        lastName: filter,
        acronym: filter,
      });
      let dataItems: DataItem[] = result.content
        ? result.content.map(
            (teacher): DataItem => ({
              id: teacher.id,
              firstName: teacher.firstName,
              lastName: teacher.lastName,
              acronym: teacher.acronym,
              tags: teacher.tags.map((tag) => tag.name),
            }),
          )
        : [];
      return {
        data: dataItems,
        totalElements: Number(result.totalElements),
      };
    } catch {
      error(400, { message: 'Could not fetch page' });
    }
  }

  async function deleteEntry(id: string) {
    try {
      await deleteTeacher(id);
    } catch {
      error(400, { message: `could not delete teacher with id ${id}` });
    }
  }
</script>

<div class="h-[170px] w-full bg-primary text-white p-4 font-medium text-2xl items-center absolute">
  <div class="mb-8 flex flex-row justify-between">
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

  {#if stats}
    <div class="w-full flex flex-row gap-14 justify-between mb-6">
      <Card text="Schüler" icon={Icons.STUDENT} number={stats.classCount} />
      <Card text="Lehrer" icon={Icons.TEACHER} number={stats.teacherCount} />
      <Card text="Stufen" icon={Icons.CLASS} number={stats.teacherCount} />
      <Card text="Räume" icon={Icons.ROOM} number={stats.roomCount} />
      <Card text="Constraints" icon={Icons.CONSTRAINT} number={stats.classCount} />
    </div>
  {/if}
  {#if pageLoaded}
    <DataTable {columnNames} {keys} {loadPage} {deleteEntry} />
  {/if}
</div>
