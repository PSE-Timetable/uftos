<script lang="ts">
  import AddResource from '$lib/components/ui/add-resource/add-resource.svelte';
  import { createTag, updateTag, type Tag, type TagRequestDto } from '$lib/sdk/fetch-client.js';
  import { error } from '@sveltejs/kit';

  export let data;
  let tag: Tag = data.tag;
  let values: string[] = [tag.name];
  let descriptions: string[] = ['Name:'];

  async function create(values: string[], _: string[]) {
    //two args needed in component
    let tagRequestDto: TagRequestDto = { tagName: values[0] };
    try {
      await createTag(tagRequestDto);
    } catch {
      error(400, { message: 'Could not create tag' });
    }
  }

  async function update(values: string[], _: string[]) {
    //two args needed in component
    let tagRequestDto: TagRequestDto = { tagName: values[0] };
    try {
      await updateTag(tag.id, tagRequestDto);
    } catch {
      error(400, { message: 'Could not update tag' });
    }
  }
</script>

<AddResource {descriptions} {values} {create} {update} createEntity={data.create} tagsAvailable={false} />
