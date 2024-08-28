<script lang="ts">
  import * as Select from '$lib/elements/ui/select';
  import type { Tag } from '$lib/sdk/fetch-client';
  import type { Selected } from 'bits-ui';

  export let tags: Tag[];
  let selectedTags: Selected<string>[] = [];
  export let selectedTagIds: string[];
  selectedTags = tags
    .filter((tag) => selectedTagIds.includes(tag.id))
    .map((tag) => ({ label: tag.name, value: tag.id }));
  selectedTagIds = selectedTags.map((tag) => tag.value);
</script>

{#if tags.length > 0}
  <Select.Root
    multiple
    selected={selectedTags}
    onSelectedChange={(s) => (selectedTagIds = s?.map((tag) => tag.value) || [])}
  >
    {#each selectedTags as tag}
      <input name={tag.label} hidden value={tag} />
    {/each}
    <Select.Trigger class="shadow-customSmall">
      <Select.Value placeholder="Tags auswählen" />
    </Select.Trigger>
    <Select.Content>
      {#each tags as { id, name }}
        <Select.Item value={id} label={name} />
      {/each}
    </Select.Content>
  </Select.Root>
{/if}
