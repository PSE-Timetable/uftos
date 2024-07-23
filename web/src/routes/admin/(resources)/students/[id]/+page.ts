import { getStudent, getTags, type Sort, type Student } from '$lib/sdk/fetch-client';
import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';

export const load = (async ({ params }) => {
  const sort: Sort = { sort: ['name,asc'] };
  const tags = await getTags(sort);
  if (params.id === 'new') {
    const student: Student = { id: 'new', firstName: '', lastName: '', tags: [] };
    return {
      student,
      tags,
      create: true,
      meta: {
        title: 'Schüler — Hinzufügen',
      },
    };
  }
  try {
    const student = await getStudent(params.id);
    return {
      student,
      tags,
      create: false,
      meta: {
        title: `Schüler — ${student.firstName} ${student.lastName}`,
      },
    };
  } catch {
    error(404, { message: `Student with id ${params.id} not found` });
  }
}) satisfies PageLoad;
