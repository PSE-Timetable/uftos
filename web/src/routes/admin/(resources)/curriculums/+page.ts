import type { PageLoad } from './$types';

export const load = (() => {
  return {
    meta: {
      title: 'Curriculums',
    },
  };
}) satisfies PageLoad;
