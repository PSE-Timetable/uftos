<script lang="ts">
  import { goto } from '$app/navigation';
  import { Button } from '$lib/elements/ui/button';
  import { Checkbox } from '$lib/elements/ui/checkbox';
  import { Input } from '$lib/elements/ui/input';
  import { getGrades, type GradeResponseDto, type Subject, type Tag } from '$lib/sdk/fetch-client';
  import { toast } from '$lib/utils/resources';
  import TagsMultipleSelect from '$lib/components/ui/tags-multiple-select/tags-multiple-select.svelte';
  import ComboBox from '$lib/elements/ui/combo-box/combo-box.svelte';
  import { error } from '@sveltejs/kit';

  export let descriptions: string[];
  export let values: string[];
  export let createEntity: boolean;
  export let create: (values: string[], tagIds: string[], subjectIds?: string[], gradeId?: string) => Promise<void>;
  export let update: (values: string[], tagIds: string[], subjectIds?: string[], gradeId?: string) => Promise<void>;
  export let tags: Tag[] | undefined = undefined;
  export let entityTags: Tag[] = [];
  export let subjects: Subject[] | undefined = undefined;
  export let entitySubjectsIds: Set<string> = new Set();
  export let grades: GradeResponseDto[] | undefined = undefined;
  export let entityGradeId: string = '';
  let selectedTagIds: string[] = tags ? tags.map((tag) => tag.id) : [];
  let selectedSubjects = (subjects || []).map((subject) => ({
    id: subject.id,
    selected: entitySubjectsIds.has(subject.id),
  }));
  let saved: boolean = false;
  let gradesAvailable: boolean = (grades || []).length > 0;

  async function updateGrades(value: string) {
    try {
      grades = await getGrades({ sort: ['name,asc'] }, { name: value });
    } catch {
      error(400, { message: 'Could not fetch grades' });
    }
  }
</script>

<div class="flex flex-col">
  <div class="mx-7 mt-7 mb-5 text-xl flex flex-col font-bold">
    {#each descriptions as description, i}
      <div class="flex flex-row items-baseline">
        <div class="my-5 flex w-40">{description}</div>
        <div class="flex flex-col gap-1 w-80">
          <Input bind:value={values[i]} background={true} class="rounded-none font-normal flex max-w-80" />
          {#if saved && !values[i].trim()}
            <p class="text-sm text-red-600">Dieses Feld darf nicht leer sein.</p>
          {/if}
        </div>
      </div>
    {/each}
  </div>

  <div class="mx-7 flex flex-col">
    {#if grades}
      <div class="flex flex-row items-baseline">
        <div class="my-5 flex w-40 text-xl font-bold">Stufe:</div>
        {#if gradesAvailable}
          <div class="flex flex-col">
            <ComboBox
              onSearch={(value) => updateGrades(value)}
              data={grades.map((grade) => ({ value: grade.id, label: grade.name }))}
              bind:selectedId={entityGradeId}
              shadow={true}
            />
            {#if saved && !entityGradeId}
              <p class="text-sm text-red-600 font-bold">Es muss eine Stufe ausgewählt werden.</p>
            {/if}
          </div>
        {:else}
          <div class="text-lg font-semibold text-red-600">Es müssen Grades vorhanden sein.</div>
        {/if}
      </div>
    {/if}
  </div>

  <div class="mx-7 flex flex-col">
    {#if subjects}
      <div class="flex flex-row mb-7 items-baseline">
        <div class="my-5 flex w-40 text-xl font-bold">Fächer:</div>
        {#if subjects.length > 0}
          <div class="flex flex-wrap bg-white rounded-md p-4 shadow-custom max-w-80 mt-5">
            {#each subjects as subject, i}
              <div class="flex items-center space-x-2 mx-1">
                <Checkbox class="m-1" bind:checked={selectedSubjects[i].selected} />
                {subject.name}
              </div>
            {/each}
          </div>
        {:else}
          <div class="text-lg font-semibold">Keine Fächer vorhanden.</div>
        {/if}
      </div>
    {/if}

    {#if tags}
      <div class="flex flex-row items-baseline">
        <div class=" flex w-40 text-xl font-bold">Tags:</div>
        {#if tags.length > 0}
          <div class="w-80 flex">
            <TagsMultipleSelect {tags} {entityTags} bind:selectedTagIds />
          </div>
        {:else}
          <div class="text-lg font-semibold">Keine Tags vorhanden.</div>
        {/if}
      </div>
    {/if}

    <div class="ml-40 mt-7 w-80 flex">
      <Button
        on:click={async () => {
          for (let value of values) {
            if (!value.trim()) {
              saved = true;
              toast(false, 'Die Eingabefelder dürfen nicht leer sein.');
              return;
            }
          }
          if (grades && !entityGradeId) {
            saved = true;
            toast(false, 'Die Eingabefelder dürfen nicht leer sein.');
            return;
          }
          let subjectIds = selectedSubjects.filter((subject) => subject.selected).map((subject) => subject.id);
          await (createEntity
            ? create(values, selectedTagIds, subjectIds, entityGradeId)
            : update(values, selectedTagIds, subjectIds, entityGradeId));
          await goto('./');
        }}
        class="max-w-52 bg-accent px-16 py-5 text-white hover:bg-accent flex"
        variant="secondary"
        disabled={grades && grades.length === 0}
      >
        Speichern
      </Button>
    </div>
  </div>
</div>
