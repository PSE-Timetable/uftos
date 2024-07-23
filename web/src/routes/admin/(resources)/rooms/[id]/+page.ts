import { getRoom, getTags, type Room, type Sort } from '$lib/sdk/fetch-client';
import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';

export const load = (async ({ params }) => {
  const sort: Sort = { sort: ['name,asc'] };
  const tags = await getTags(sort);
  if (params.id === 'new') {
    const room: Room = { id: 'new', name: '', buildingName: '', capacity: 0, tags: [] };
    return {
      room,
      tags,
      create: true,
      meta: {
        title: 'Raum — Hinzufügen',
      },
    };
  }
  try {
    const room = await getRoom(params.id);
    return {
      room,
      tags,
      create: false,
      meta: {
        title: `Raum — ${room.name}`,
      },
    };
  } catch {
    error(404, { message: `Room with id ${params.id} not found` });
  }
}) satisfies PageLoad;
