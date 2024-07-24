<script lang="ts">
  import AddResource from '$lib/components/ui/add-resource/add-resource.svelte';
  import { createRoom, updateRoom, type Room, type RoomRequestDto, type Tag } from '$lib/sdk/fetch-client.js';
  import { error } from '@sveltejs/kit';

  export let data;
  let room: Room = data.room;
  let values: string[] = [room.name, room.buildingName, String(room.capacity)];
  let descriptions: string[] = ['Name:', 'Gebäudename:', 'Kapazität:'];

  async function create(values: string[], tagIds: string[]) {
    let roomRequestDto: RoomRequestDto = {
      name: values[0],
      buildingName: values[1],
      capacity: Number(values[2]),
      tagIds,
    };
    try {
      await createRoom(roomRequestDto);
    } catch {
      error(400, { message: 'Could not create student' });
    }
  }

  async function update(values: string[], tagIds: string[]) {
    let roomRequestDto: RoomRequestDto = {
      name: values[0],
      buildingName: values[1],
      capacity: Number(values[2]),
      tagIds,
    };
    try {
      await updateRoom(room.id, roomRequestDto);
    } catch {
      error(400, { message: 'Could not update student' });
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
  entityTags={room.tags}
/>
