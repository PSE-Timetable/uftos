<script lang="ts">
  import AddResource from '$lib/components/ui/add-resource/add-resource.svelte';
  import { createRoom, updateRoom, type RoomRequestDto } from '$lib/sdk/fetch-client.js';
  import { error } from '@sveltejs/kit';
  import { _schema as schema } from './+page';

  export let data;
  let descriptions: string[] = ['Name:', 'Gebäudename:'];

  async function update(formData: Record<string, string | number | string[]>) {
    let roomRequestDto: RoomRequestDto = {
      name: String(formData.name),
      buildingName: String(formData.buildingName),
      capacity: Number(formData.capacity),
      tagIds: formData.tags as string[],
    };
    try {
      const _ =
        formData.id === 'new'
          ? await createRoom(roomRequestDto)
          : await updateRoom(String(formData.id), roomRequestDto);
    } catch {
      error(400, { message: 'Could not update student' });
    }
  }
</script>

<AddResource {descriptions} data={data.form} {schema} {update} tags={data.tags} />
