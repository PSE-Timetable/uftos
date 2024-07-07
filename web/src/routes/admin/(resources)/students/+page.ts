import { getStudents, type Pageable, type PageStudent } from '$lib/sdk/fetch-client';
import type { PageLoad } from './$types';

export const load = (async () => {
  const pageable: Pageable = { page: 0, size: 10 };
  const result: PageStudent = await getStudents(pageable);
  const students = result.content;
  return {
    students,
    meta: {
      title: 'Students',
    },
  };
}) satisfies PageLoad;
