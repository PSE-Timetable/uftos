<script lang="ts">
  import AddResource from '$lib/components/ui/add-resource/add-resource.svelte';
  import { createSubject, updateSubject, type Subject, type SubjectRequestDto } from '$lib/sdk/fetch-client.js';
  import { error } from '@sveltejs/kit';

  export let data;
  let subject: Subject = data.subject;
  let values: string[] = [subject.name];
  let descriptions: string[] = ['Name:'];

  async function create(values: string[], tagIds: string[]) {
    let subjectRequestDto: SubjectRequestDto = {
      name: values[0],
      tagIds,
    };
    try {
      await createSubject(subjectRequestDto);
    } catch {
      error(400, { message: 'Could not create subject' });
    }
  }

  async function update(values: string[], tagIds: string[]) {
    let subjectRequestDto: SubjectRequestDto = {
      name: values[0],
      tagIds,
    };
    try {
      await updateSubject(subject.id, subjectRequestDto);
    } catch {
      error(400, { message: 'Could not update subject' });
    }
  }
</script>

<AddResource
  {descriptions}
  {values}
  {create}
  {update}
  createEntity={data.create}
  tags={data.tags}
  entityTags={subject.tags}
/>
