<script lang="ts">
  import { goto } from '$app/navigation';
  import { Button } from '$lib/elements/ui/button';
  import { Checkbox } from '$lib/elements/ui/checkbox';
  import { Input } from '$lib/elements/ui/input';
  import type { Subject, Tag } from '$lib/sdk/fetch-client';
  import TagsMultipleSelect from '../tags-multiple-select/tags-multiple-select.svelte';

  export let descriptions: string[];
  export let values: string[];
  export let createEntity: boolean;
  export let create: (values: string[], tagIds: string[], subjectIds?: string[]) => Promise<void>;
  export let update: (values: string[], tagIds: string[]) => Promise<void>;
  export let tags: Tag[];
  export let entityTags: Tag[];
  export let subjects: Subject[] = [];
  export let enitySubjects: Subject[] = [];
  let selectedTagIds: string[] = tags.map((tag) => tag.id);
  let selectedSubject: boolean[] = [];
</script>

<div class="flex flex-row">
  <div class="m-7 text-xl flex flex-col font-bold">
    {#each descriptions as description}
      <div class="my-5 flex">{description}</div>
    {/each}
  </div>
  <div class="mx-7 my-7 flex flex-col w-80">
    {#each values as value}
      <Input
        bind:value
        class="rounded-none border-0 border-b-4 border-foreground focus-visible:ring-0 focus-visible:border-b-4 text-lg font-normal mt-4 mb-3 flex"
      />
    {/each}
    {#if subjects.length > 0}
      <div class="flex flex-wrap bg-white rounded-md">
        {#each subjects as subject}
          <div class="flex items-center space-x-2 mx-1">
            <Checkbox class="m-1" />
            {subject.name}
          </div>{/each}
      </div>
    {/if}
    <div class="max-w-sm mt-8 flex">
      <TagsMultipleSelect {tags} {entityTags} bind:selectedTagIds />
    </div>
    <Button
      on:click={async () => {
        createEntity ? await create(values, selectedTagIds) : await update(values, selectedTagIds);
        await goto('./');
      }}
      class="max-w-52 px-10 mt-10 bg-accent text-white flex"
      variant="secondary"
    >
      Speichern
    </Button>
  </div>
</div>
