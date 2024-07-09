<script lang="ts">
    import ConstraintSignatureComp from '$lib/components/ui/constraintSignature/constraint-signature.svelte';
    import {
        type ConstraintParameter,
        type ConstraintSignature,
        DefaultType, type Grade,
        ParameterType,
        type Subject,
        type Tag,
        type Teacher
    } from "$lib/sdk/fetch-client";

    const constraintParameters: ConstraintParameter[] = [
        {
            id: '123',
            parameterName: 'Klasse X',
            parameterType: ParameterType.Grade
        },
        {
            id: '456',
            parameterName: 'Lehrer Y',
            parameterType: ParameterType.Teacher
        },
        {
            id: '789',
            parameterName: 'Fach Y',
            parameterType: ParameterType.Subject
        }
    ];

    const constraintSignature: ConstraintSignature = {
        parameters: constraintParameters,
        description: 'Klasse X hat Lehrer Y im Fach Z',
        name: 'test_constraint',
        defaultType: DefaultType.HardPenalize
    }

    const tag: Tag = {
        id: '123',
        name: 'qwerty'
    }

    const subjects: Subject[] = [
        {
            id: '123',
            name: 'Deutsch',
            tags: [ tag ]
        },
        {
            id: '456',
            name: 'English',
            tags: [ tag ]
        }
    ];

    const grades: Grade[] = [
        {
            id: '123',
            name: '5.'
        },
        {
            id: '456',
            name: '6.'
        }
    ]

    const teachers: Teacher[] = [
        {
            acronym: 'JS',
            firstName: 'John',
            id: '1',
            lastName: 'Smith',
            tags: [ tag ],
            subjects: [ subjects[0] ]
        },
        {
            acronym: 'AM',
            firstName: 'Avocado',
            id: '2',
            lastName: 'Mister',
            tags: [ tag ],
            subjects: [ subjects[1] ]
        }
    ];
</script>
<ConstraintSignatureComp teachers={teachers} grades={grades} subjects={subjects} constraintSignature={constraintSignature}/>