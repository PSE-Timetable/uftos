import type { PageLoad } from './$types';

export const load = (() => {
  return {
    meta: {
      title: 'Tagmanagment',
    },
  };
}) satisfies PageLoad;
