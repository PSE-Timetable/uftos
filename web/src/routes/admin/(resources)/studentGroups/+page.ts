import { getGrades, getStudentGroups, getStudents } from '$lib/sdk/fetch-client';
import { init } from '$lib/utils/server';
import type { PageLoad } from './$types';

export const load = (async ({ fetch }) => {
  init(fetch);
  const { totalElements: size } = await getStudentGroups({ page: 0, size: 1 });
  return {
    studentGroups: await getStudentGroups({ page: 0, size, sort: ['name,asc'] }).then(({ content }) => content),
    students: await getStudents({ page: 0, size: 40 }).then(({ content }) => content),
    grades: await getGrades({ sort: ['name,asc'] }),
    meta: {
      title: 'Schülergruppen',
    },
  };
}) satisfies PageLoad;
