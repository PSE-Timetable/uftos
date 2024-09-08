import { init } from '$lib/utils/server';
import type { PageLoad } from './$types';

export const load = (({ fetch }) => {
  init(fetch);
  return {
    meta: {
      title: 'UFTOS',
    },
  };
}) satisfies PageLoad;
