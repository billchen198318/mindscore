import { useRouter } from 'vue-router';
import { getProgItem, getUrlPrefixFromProgItem } from '@/components/BaseHelper';

export const useActionSourceNavigation = () => {
    const router = useRouter();

    const createActionFromSource = async (sourceType: string, sourceOid: string, sourceName: string) => {
        const createProgram = getProgItem('MD_PROG008D0001A');
        const path = getUrlPrefixFromProgItem(createProgram);
        if (!path || !sourceType || !sourceOid) {
            return false;
        }
        await router.push({
            path,
            query: {
                sourceType,
                sourceOid,
                sourceName
            }
        });
        return true;
    };

    return { createActionFromSource };
};
