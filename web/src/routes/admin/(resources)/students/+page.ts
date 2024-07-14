import type { PageLoad } from './$types';

export const load = (() => {
  return {
    meta: {
      title: 'Schüler',
    },
  };
}) satisfies PageLoad;
