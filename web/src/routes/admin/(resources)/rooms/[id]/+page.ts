import { getRoom } from '$lib/sdk/fetch-client';
import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';

export const load = (async ({ params }) => {
  try {
    const room = await getRoom(params.id);
    return {
      room,
      meta: {
        title: `Room — ${room.name}`,
      },
    };
  } catch {
    error(404, { message: `Room with id ${params.id} not found` });
  }
}) satisfies PageLoad;
