import { getStudentGroup, getSubjects, getTags, type Sort, type StudentGroup } from '$lib/sdk/fetch-client';
import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';

export const load = (async ({ params }) => {
  const sort: Sort = { sort: ['name,asc'] };
  const tags = await getTags(sort);
  const subjects = await getSubjects(sort);
  if (params.id === 'new') {
    const studentGroup: StudentGroup = { id: 'new', name: '', grades: [], students: [], tags: [], subjects: [] };
    return {
      studentGroup,
      tags,
      subjects,
      create: true,
      meta: {
        title: 'Schülergruppe — Hinzufügen',
      },
    };
  }
  try {
    const studentGroup = await getStudentGroup(params.id);
    return {
      studentGroup,
      tags,
      subjects,
      create: false,
      meta: {
        title: `Schülergruppe — ${studentGroup.name}`,
      },
    };
  } catch {
    error(404, { message: `Student group with id ${params.id} not found` });
  }
}) satisfies PageLoad;
