import {
  createCurriculum,
  getCurriculum,
  getGrades,
  getSubjects,
  updateCurriculum,
  type CurriculumRequestDto,
  type LessonsCount,
  type Sort,
} from '$lib/sdk/fetch-client';
import type { PageLoad } from './$types';

export const load = (async () => {
  const sort: Sort = { sort: ['name,asc'] };
  let grades = await getGrades(sort);
  const subjects = await getSubjects(sort);

  for (const grade of grades) {
    if (grade.curriculumId) {
      const result = await getCurriculum(grade.curriculumId);
      const addLessonsCount: LessonsCount[] = [];
      for (const subject of subjects) {
        if (!result.lessonsCounts.some((lessonsCount) => lessonsCount.subject?.id === subject.id)) {
          addLessonsCount.push({ count: 0, id: '', subject });
        }
      }
      result.lessonsCounts = result.lessonsCounts.concat(addLessonsCount);
      await updateCurriculum(grade.curriculumId, {
        name: result.name,
        gradeId: result.grade.id,
        lessonsCounts: result.lessonsCounts
          .concat(addLessonsCount)
          .map((lessonsCount) => ({ count: lessonsCount.count!, subjectId: lessonsCount.subject!.id })),
      });
    } else {
      const curriculumRequestDto: CurriculumRequestDto = {
        gradeId: grade.id,
        lessonsCounts: subjects.map((subject) => ({ count: 0, subjectId: subject.id })),
        name: grade.name,
      };
      const result = await createCurriculum(curriculumRequestDto);
      grade.curriculumId = result.id;
    }
  }
  grades = await getGrades({ sort: ['name,asc'] });

  return {
    grades,
    meta: {
      title: 'Curriculum anpassen',
    },
  };
}) satisfies PageLoad;
