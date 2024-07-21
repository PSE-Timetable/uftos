import type { PageLoad } from './$types';

export const load = (() => {
  return {
    meta: {
      title: 'Curriculum anpassen',
    },
  };
}) satisfies PageLoad;
