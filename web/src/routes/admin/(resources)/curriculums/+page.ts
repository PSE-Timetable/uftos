import { getGrades, type Sort } from '$lib/sdk/fetch-client';
import type { PageLoad } from './$types';

export const load = (async () => {
  const sort: Sort = { sort: ['name,asc'] };
  const grades = await getGrades(sort);

  return {
    grades,
    meta: {
      title: 'Curriculum anpassen',
    },
  };
}) satisfies PageLoad;
