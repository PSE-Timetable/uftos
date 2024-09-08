import { loadRoomPage } from '$lib/utils/resources';
import { init } from '$lib/utils/server';
import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';

export const load = (async () => {
  init();
  try {
    return {
      initialData: await loadRoomPage('', '', 0, 15),
      meta: {
        title: 'Räume',
      },
    };
  } catch {
    error(400, { message: 'Could not fetch page' });
  }
}) satisfies PageLoad;
