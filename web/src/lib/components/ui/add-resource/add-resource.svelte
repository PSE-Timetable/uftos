<script lang="ts">
  import { goto } from '$app/navigation';
  import { Button } from '$lib/elements/ui/button';
  import { Checkbox } from '$lib/elements/ui/checkbox';
  import { Input } from '$lib/elements/ui/input';
  import type { Subject, Tag } from '$lib/sdk/fetch-client';
  import { toast } from '$lib/utils/resources';
  import TagsMultipleSelect from '$lib/components/ui/tags-multiple-select/tags-multiple-select.svelte';

  export let descriptions: string[];
  export let values: string[];
  export let createEntity: boolean;
  export let create: (values: string[], tagIds: string[], subjectIds?: string[]) => Promise<void>;
  export let update: (values: string[], tagIds: string[], subjectIds?: string[]) => Promise<void>;
  export let tags: Tag[] | undefined = undefined;
  export let entityTags: Tag[] = [];
  export let subjects: Subject[] | undefined = undefined;
  export let entitySubjectsIds: Set<string> = new Set();
  let selectedTagIds: string[] = tags ? tags.map((tag) => tag.id) : [];
  let selectedSubjects = (subjects || []).map((subject) => ({
    id: subject.id,
    selected: entitySubjectsIds.has(subject.id),
  }));
  let saved: boolean = false;
</script>

<div class="grid grid-cols-[max-content,1fr] gap-8 p-4">
  {#each descriptions as description, i}
    <div class="text-lg font-bold flex">{description}</div>
    <div class="flex flex-col gap-1 w-80">
      <Input bind:value={values[i]} background={true} class="rounded-none font-normal flex max-w-80" />
      {#if saved && !values[i].trim()}
        <p class="text-sm text-red-600">Dieses Feld darf nicht leer sein.</p>
      {/if}
    </div>
  {/each}

  {#if subjects}
    <div class="flex text-lg font-bold">Fächer:</div>
    {#if subjects.length > 0}
      <div class="flex flex-wrap bg-white rounded-md gap-2 p-4 shadow-custom max-w-80">
        {#each subjects as subject, i}
          <div class="flex items-top space-x-2">
            <Checkbox class="m-1" bind:checked={selectedSubjects[i].selected} />
            <p class="break-all w-full">
              {subject.name}
            </p>
          </div>
        {/each}
      </div>
    {:else}
      <div class="text-lg font-semibold">Keine Fächer vorhanden.</div>
    {/if}
  {/if}

  {#if tags}
    <div class=" flex text-lg font-bold">Tags:</div>
    {#if tags.length > 0}
      <div class="w-80 flex">
        <TagsMultipleSelect {tags} {entityTags} bind:selectedTagIds />
      </div>
    {:else}
      <div class="text-lg font-semibold">Keine Tags vorhanden.</div>
    {/if}
  {/if}

  <Button
    on:click={async () => {
      for (let value of values) {
        if (!value.trim()) {
          saved = true;
          toast(false, 'Die Eingabefelder dürfen nicht leer sein.');
          return;
        }
      }
      let subjectIds = selectedSubjects.filter((subject) => subject.selected).map((subject) => subject.id);
      await (createEntity ? create(values, selectedTagIds, subjectIds) : update(values, selectedTagIds, subjectIds));
      await goto('./');
    }}
    class="col-start-2 p-8 text-lg w-80 bg-accent text-white hover:bg-accent flex"
    variant="secondary"
  >
    Speichern
  </Button>
</div>
