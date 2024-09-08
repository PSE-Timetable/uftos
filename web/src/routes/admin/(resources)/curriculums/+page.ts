import { getGrades, type Sort } from '$lib/sdk/fetch-client';
import { init } from '$lib/utils/server';
import type { PageLoad } from './$types';

export const load = (async () => {
  init();
  const sort: Sort = { sort: ['name,asc'] };
  const grades = await getGrades(sort);

  return {
    grades,
    meta: {
      title: 'Curriculum anpassen',
    },
  };
}) satisfies PageLoad;
