import type { PageLoad } from './$types';

export const load = (() => {
  return {
    meta: {
      title: 'Stufen',
    },
  };
}) satisfies PageLoad;
