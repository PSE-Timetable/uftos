<script lang="ts">
  import AddResource from '$lib/components/ui/add-resource/add-resource.svelte';
  import { createTag, updateTag, type TagRequestDto } from '$lib/sdk/fetch-client.js';
  import { error } from '@sveltejs/kit';
  import { _schema as schema } from './+page';

  export let data;
  let descriptions: string[] = ['Name:'];

  async function update(formData: Record<string, string | number | string[]>) {
    let tagRequestDto: TagRequestDto = { tagName: String(formData.name) };
    try {
      const _ =
        formData.id === 'new' ? await createTag(tagRequestDto) : await updateTag(String(formData.id), tagRequestDto);
    } catch {
      error(400, { message: 'Could not update tag' });
    }
  }
</script>

<AddResource {descriptions} data={data.form} {schema} {update} />
