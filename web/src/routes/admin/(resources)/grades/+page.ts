import { loadGrades } from '$lib/utils/resources';
import type { PageLoad } from './$types';

export const load = (async () => {
  return {
    initialData: await loadGrades('', ''),
    meta: {
      title: 'Stufen',
    },
  };
}) satisfies PageLoad;
