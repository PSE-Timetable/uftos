<script lang="ts">
  import { goto } from '$app/navigation';
  import { Button } from '$lib/elements/ui/button';
  import { Checkbox } from '$lib/elements/ui/checkbox';
  import { Input } from '$lib/elements/ui/input';
  import type { Subject, Tag } from '$lib/sdk/fetch-client';
    import { toast } from '$lib/utils/resources';
  import TagsMultipleSelect from '../tags-multiple-select/tags-multiple-select.svelte';

  export let descriptions: string[];
  export let values: string[];
  export let createEntity: boolean;
  export let create: (values: string[], tagIds: string[], subjectIds?: string[]) => Promise<void>;
  export let update: (values: string[], tagIds: string[], subjectIds?: string[]) => Promise<void>;
  export let tags: Tag[];
  export let entityTags: Tag[];
  export let subjects: Subject[] = [];
  export let entitySubjectsIds: Set<string> = new Set();
  let selectedTagIds: string[] = tags.map((tag) => tag.id);
  let selectedSubjects = subjects.map((subject) => ({ id: subject.id, selected: entitySubjectsIds.has(subject.id) }));
  let saved: boolean = false;
</script>

<div class="flex flex-col">
  <div class="m-7 text-xl flex flex-col font-bold">
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
    {#if subjects.length > 0}
      <div class="flex flex-row mb-7">
        <div class="my-5 flex w-40 text-xl font-bold">Fächer:</div>
        <div class="flex flex-wrap bg-white rounded-md p-4 shadow-custom max-w-80">
          {#each subjects as subject, i}
            <div class="flex items-center space-x-2 mx-1">
              <Checkbox class="m-1" bind:checked={selectedSubjects[i].selected} />
              {subject.name}
            </div>
          {/each}
        </div>
      </div>
    {/if}
    {#if tags.length > 0}
      <div class="flex flex-row items-baseline">
        <div class=" flex w-40 text-xl font-bold">Tags:</div>
        <div class="w-80 flex">
          <TagsMultipleSelect {tags} {entityTags} bind:selectedTagIds />
        </div>
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
          let subjectIds = selectedSubjects.filter((subject) => subject.selected).map((subject) => subject.id);
          await (createEntity
            ? create(values, selectedTagIds, subjectIds)
            : update(values, selectedTagIds, subjectIds));
          await goto('./');
        }}
        class="max-w-52 bg-accent px-16 py-5 text-white hover:bg-accent flex"
        variant="secondary"
      >
        Speichern
      </Button>
    </div>
  </div>
</div>
