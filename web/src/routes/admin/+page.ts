import { loadGrades, loadRoomPage, loadStudentPage, loadTeacherPage } from '$lib/utils/resources';
import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';

export const load = (async () => {
  try {
    return {
      initialGrades: await loadGrades('', ''),
      initialRooms: await loadRoomPage('', '', 0, 15),
      initialStudents: await loadStudentPage('', '', 0, 15),
      initialTeachers: await loadTeacherPage('', '', 0, 15),
      meta: {
        title: 'Übersicht',
      },
    };
  } catch {
    error(400, { message: 'Could not fetch page' });
  }
}) satisfies PageLoad;
