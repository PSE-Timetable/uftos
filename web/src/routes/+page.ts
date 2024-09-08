import { init } from '$lib/utils/server';
import type { PageLoad } from './$types';

export const load = (() => {
  init();
  return {
    meta: {
      title: 'UFTOS',
    },
  };
}) satisfies PageLoad;
