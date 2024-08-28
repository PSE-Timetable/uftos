<script lang="ts">
  import AddResource from '$lib/components/ui/add-resource/add-resource.svelte';
  import { createStudent, updateStudent, type StudentRequestDto } from '$lib/sdk/fetch-client.js';
  import { error } from '@sveltejs/kit';
  import { _schema as schema } from './+page';

  export let data;
  let descriptions: string[] = ['Vorname:', 'Nachname:'];

  async function update(formData: Record<string, string | number | string[]>) {
    let studentRequestDto: StudentRequestDto = {
      firstName: String(formData.firstName),
      lastName: String(formData.lastName),
      groupIds: [], // need to be removed after merge of #377
      tagIds: formData.tags as string[],
    };
    try {
      const _ = //eslint complains otherwise (no-unused-expressions)
        formData.id === 'new'
          ? await createStudent(studentRequestDto)
          : await updateStudent(String(formData.id), studentRequestDto);
    } catch {
      error(400, { message: 'Could not update student' });
    }
  }
</script>

<AddResource {descriptions} data={data.form} {schema} {update} tags={data.tags} />
