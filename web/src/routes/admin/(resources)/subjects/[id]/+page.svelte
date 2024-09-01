<script lang="ts">
  import AddResource from '$lib/components/ui/add-resource/add-resource.svelte';
  import { createSubject, updateSubject, type SubjectRequestDto } from '$lib/sdk/fetch-client.js';
  import { error } from '@sveltejs/kit';
  import { _schema as schema } from './+page';

  export let data;
  let descriptions: string[] = ['Name:'];

  async function update(formData: Record<string, string | number | string[]>) {
    let subjectRequestDto: SubjectRequestDto = {
      name: String(formData.name),
      tagIds: formData.tags as string[],
    };
    try {
      const _ =
        formData.id === 'new'
          ? await createSubject(subjectRequestDto)
          : await updateSubject(String(formData.id), subjectRequestDto);
    } catch {
      error(400, { message: 'Could not update subject' });
    }
  }
</script>

<AddResource {descriptions} data={data.form} {schema} {update} tags={data.tags} />
