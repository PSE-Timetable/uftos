<script lang="ts">
  import * as Select from '$lib/elements/ui/select';
  import type { Tag } from '$lib/sdk/fetch-client';
  import type { Selected } from 'bits-ui';

  export let tags: Tag[];
  let selectedTags: Selected<unknown>[] = [];
  export let selectedTagIds;
</script>

<Select.Root
  multiple
  selected={selectedTags}
  onSelectedChange={(s) => (selectedTagIds = s?.map((tag) => tag.value) || [])}
>
  {#each selectedTags as tag}
    <input name={tag.label} hidden value={tag} />
  {/each}
  <Select.Trigger>
    <Select.Value placeholder="Tags auswählen" />
  </Select.Trigger>
  <Select.Content>
    {#each tags as { id, name }}
      <Select.Item value={id} label={name} />
    {/each}
  </Select.Content>
</Select.Root>
