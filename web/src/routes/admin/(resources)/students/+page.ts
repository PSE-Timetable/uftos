import { loadStudentPage } from '$lib/utils/resources';
import type { PageLoad } from './$types';

export const load = (async () => {
  return {
    initialData: await loadStudentPage('', '', 0),
    meta: {
      title: 'Schüler',
    },
  };
}) satisfies PageLoad;
