import {
  getGrades,
  getStudentGroup,
  getSubjects,
  getTags,
  type Sort,
  type StudentGroupResponseDto,
} from '$lib/sdk/fetch-client';
import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';

export const load = (async ({ params }) => {
  const sort: Sort = { sort: ['name,asc'] };
  const tags = await getTags(sort);
  const subjects = await getSubjects(sort);
  const grades = await getGrades(sort);
  if (params.id === 'new') {
    const studentGroup: StudentGroupResponseDto = {
      id: 'new',
      name: '',
      grades: [],
      students: [],
      tags: [],
      subjects: [],
      lessons: [],
    };
    return {
      studentGroup,
      tags,
      subjects,
      grades,
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
      grades,
      create: false,
      meta: {
        title: `Schülergruppe — ${studentGroup.name}`,
      },
    };
  } catch {
    error(404, { message: `Student group with id ${params.id} not found` });
  }
}) satisfies PageLoad;
