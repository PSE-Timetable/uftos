import { getStudentGroups } from '$lib/sdk/fetch-client';
import type { PageLoad } from './$types';

export const load = (async () => {
  return {
    studentGroups: await getStudentGroups({ page: 0, size: 50 }).then(({ content }) => content),
    meta: {
      title: 'Schülergruppen',
    },
  };
}) satisfies PageLoad;
