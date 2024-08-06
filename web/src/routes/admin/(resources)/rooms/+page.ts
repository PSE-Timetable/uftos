import { loadRoomPage } from '$lib/utils/resources';
import type { PageLoad } from './$types';

export const load = (async () => {
  return {
    initialData: await loadRoomPage('', '', 0),
    meta: {
      title: 'Räume',
    },
  };
}) satisfies PageLoad;
