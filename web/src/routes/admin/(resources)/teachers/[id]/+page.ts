import { getSubjects, getTags, getTeacher, type Sort, type Teacher } from '$lib/sdk/fetch-client';
import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';

export const load = (async ({ params }) => {
  const sort: Sort = { sort: ['name,asc'] };
  const tags = await getTags(sort);
  const subjects = await getSubjects(sort);
  if (params.id === 'new') {
    const teacher: Teacher = { id: 'new', firstName: '', lastName: '', acronym: '', subjects: [], tags: [] };
    return {
      teacher,
      tags,
      subjects,
      create: true,
      meta: {
        title: 'Lehrer — Hinzufügen',
      },
    };
  }
  try {
    const teacher = await getTeacher(params.id);
    return {
      teacher,
      tags,
      subjects,
      create: false,
      meta: {
        title: `Teacher — ${teacher.firstName} ${teacher.lastName} (${teacher.acronym})`,
      },
    };
  } catch {
    error(404, { message: `Teacher with id ${params.id} not found` });
  }
}) satisfies PageLoad;
