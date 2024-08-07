import { getGrades, getStudentGroups, getStudents } from '$lib/sdk/fetch-client';
import type { PageLoad } from './$types';

export const load = (async () => {
  return {
    studentGroups: await getStudentGroups({ page: 0, size: 50, sort: ['name,asc'] }).then(({ content }) => content),
    students: await getStudents({ page: 0, size: 40 }).then(({ content }) => content),
    grades: await getGrades({ sort: ['name,asc'] }),
    meta: {
      title: 'Schülergruppen',
    },
  };
}) satisfies PageLoad;
