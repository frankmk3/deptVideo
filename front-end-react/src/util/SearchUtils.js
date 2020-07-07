export function getFiltersAsQuery(filters) {
    var query = '';
    if (filters !== undefined) {
        query = Object.keys(filters)
            .filter(key => filters[key])
            .filter(key => filters[key].length > 0)
            .map(key => filters[key].map(item => key + ':' + item + '*').join(" OR "))
            .join(" AND ");
    }
    return query;
}