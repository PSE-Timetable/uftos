import { getGrades, getStudentGroups, getStudents } from '$lib/sdk/fetch-client';
import type { PageLoad } from './$types';

export const load = (async () => {
  const size = await getStudentGroups({ page: 0, size: 1 }).then(({ totalElements }) => totalElements);
  return {
    studentGroups: await getStudentGroups({ page: 0, size, sort: ['name,asc'] }).then(({ content }) => content),
    students: await getStudents({ page: 0, size: 40 }).then(({ content }) => content),
    grades: await getGrades({ sort: ['name,asc'] }),
    meta: {
      title: 'Schülergruppen',
    },
  };
}) satisfies PageLoad;
