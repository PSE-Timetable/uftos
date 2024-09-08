import { getRoom, getTags, type Sort } from '$lib/sdk/fetch-client';
import { init } from '$lib/utils/server';
import { error } from '@sveltejs/kit';
import { superValidate } from 'sveltekit-superforms';
import { zod } from 'sveltekit-superforms/adapters';
import { z } from 'zod';
import type { PageLoad } from './$types';

export const _schema = z.object({
  id: z.string(),
  name: z.string().trim().min(1, { message: 'Der Name darf nicht leer sein.' }),
  buildingName: z.string().trim().min(1, { message: 'Der Gebäudename darf nicht leer sein.' }),
  capacity: z.coerce.number().int().positive({ message: 'Die Kapazität darf nicht 0 sein.' }),
  tags: z.string().array(),
});

export const load = (async ({ params, fetch }) => {
  init(fetch);
  const sort: Sort = { sort: ['name,asc'] };
  const tags = await getTags(sort);
  let formRoom: { id: string; name: string; buildingName: string; capacity: number; tags: string[] }, title: string;
  if (params.id === 'new') {
    formRoom = { id: 'new', name: '', buildingName: '', capacity: 0, tags: [] };
    title = 'Raum — Hinzufügen';
  } else {
    try {
      const room = await getRoom(params.id);
      title = `Raum — ${room.name}`;
      formRoom = {
        ...room,
        tags: room.tags.map((tag) => tag.id),
      };
    } catch {
      error(404, { message: `Room with id ${params.id} not found` });
    }
  }
  return {
    form: await superValidate(formRoom, zod(_schema), { errors: false }),
    tags,
    meta: {
      title,
    },
  };
}) satisfies PageLoad;
