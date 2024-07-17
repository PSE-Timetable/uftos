import type { PageLoad } from './$types';

export const load = (() => {
  return {
    meta: {
      title: 'Fächer',
    },
  };
}) satisfies PageLoad;
