import { loadGrades, loadRoomPage, loadStudentPage, loadTeacherPage } from '$lib/utils/resources';
import type { PageLoad } from './$types';

export const load = (async () => {
  return {
    initialGrades: await loadGrades('', ''),
    initialRooms: await loadRoomPage('', '', 0),
    initialStudents: await loadStudentPage('', '', 0),
    initialTeachers: await loadTeacherPage('', '', 0),
    meta: {
      title: 'Übersicht',
    },
  };
}) satisfies PageLoad;
