import type { LayoutLoad } from './$types';

export const load = (() => {
  return {
    meta: {
      title: 'UFTOS',
    },
  };
}) satisfies LayoutLoad;
