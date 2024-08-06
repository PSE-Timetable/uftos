import { loadTeacherPage } from '$lib/utils/resources';
import type { PageLoad } from './$types';

export const load = (async () => {
  return {
    initialData: await loadTeacherPage('', '', 0),
    meta: {
      title: 'Lehrer',
    },
  };
}) satisfies PageLoad;
