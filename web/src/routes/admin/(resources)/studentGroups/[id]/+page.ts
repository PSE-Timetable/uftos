import { getGrades, getStudentGroup, getSubjects, getTags, type Sort } from '$lib/sdk/fetch-client';
import { init } from '$lib/utils/server';
import { error } from '@sveltejs/kit';
import { superValidate } from 'sveltekit-superforms';
import { zod } from 'sveltekit-superforms/adapters';
import { z } from 'zod';
import type { PageLoad } from './$types';

export const _schema = z.object({
  id: z.string(),
  name: z.string().trim().min(1, { message: 'Der Name darf nicht leer sein.' }),
  grades: z.string().array().min(1, { message: 'Es muss eine Stufe ausgewählt werden.' }),
  students: z.string().array(),
  tags: z.string().array(),
});

export const load = (async ({ params }) => {
  init();
  const sort: Sort = { sort: ['name,asc'] };
  const tags = await getTags(sort);
  const subjects = await getSubjects(sort);
  const grades = await getGrades(sort);
  let formGroup: { id: string; name: string; grades: string[]; students: string[]; tags: string[] },
    title: string,
    groupSubjects: string[];
  if (params.id === 'new') {
    formGroup = { id: 'new', name: '', grades: [], students: [], tags: [] };
    title = 'Schülergruppe — Hinzufügen';
    groupSubjects = [];
  } else {
    try {
      const studentGroup = await getStudentGroup(params.id);
      formGroup = {
        ...studentGroup,
        grades: studentGroup.grades.map((grade) => grade.id),
        students: studentGroup.students.map((student) => student.id),
        tags: studentGroup.tags.map((tag) => tag.id),
      };
      title = `Schülergruppe — ${studentGroup.name}`;
      groupSubjects = studentGroup.subjects.map((subject) => subject.id);
    } catch {
      error(404, { message: `Student group with id ${params.id} not found` });
    }
  }
  return {
    form: await superValidate(formGroup, zod(_schema), { errors: false }),
    tags,
    subjects,
    groupSubjects,
    grades,
    meta: {
      title,
    },
  };
}) satisfies PageLoad;
